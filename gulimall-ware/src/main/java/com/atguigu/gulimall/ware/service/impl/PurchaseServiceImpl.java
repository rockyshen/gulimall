package com.atguigu.gulimall.ware.service.impl;

import com.atguigu.common.constant.WareConstant;
import com.atguigu.gulimall.ware.entity.PurchaseDetailEntity;
import com.atguigu.gulimall.ware.service.PurchaseDetailService;
import com.atguigu.gulimall.ware.service.WareSkuService;
import com.atguigu.gulimall.ware.vo.MergeVo;
import com.atguigu.gulimall.ware.vo.PurchaseDoneVo;
import com.atguigu.gulimall.ware.vo.PurchaseItemDoneVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.ware.dao.PurchaseDao;
import com.atguigu.gulimall.ware.entity.PurchaseEntity;
import com.atguigu.gulimall.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Autowired
    private PurchaseDetailService purchaseDetailService;

    @Autowired
    private WareSkuService wareSkuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 在采购需求中，多选多个采购需求，合并成一个采购单时；
     * 查询所有未领取的采购单（已经被采购人员认领的采购单，就不能修改了）
     * @param params
     * @return
     */
    @Override
    public PageUtils queryPageUnreceivePurchase(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>().eq("status",0).or().eq("status",1)
        );
        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void mergePurchase(MergeVo mergeVo) {
        // 1.如果没有指定采购单，就新建一个采购单 purchase；如果有采购单，这个purchaseId就是这张单子的id
        Long purchaseId = mergeVo.getPurchaseId();
        if (mergeVo.getPurchaseId() == null) {
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.CREATED.getCode());  // 0:采购单新建状态
            this.save(purchaseEntity);
            purchaseId = purchaseEntity.getId();
        }

        // TODO 如果已经有采购单了，检查purchase的status必须是0或1才能下一步
        PurchaseEntity purchaseEntity1 = this.getById(purchaseId);
        if(purchaseEntity1.getStatus()== WareConstant.PurchaseStatusEnum.CREATED.getCode() ||
                purchaseEntity1.getStatus() == WareConstant.PurchaseStatusEnum.ASSIGNED.getCode()){
            // 2.如果指定了采购单：a.修改purchase_detail表中的purchase_id  b.修改purcase_detail表中的status
            Long finalPurchaseId = purchaseId;
            List<Long> items = mergeVo.getItems();
            List<PurchaseDetailEntity> collect = items.stream().map(item -> {
                PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
                detailEntity.setId(item);   // update时，一定要给实体类setId，否则不知道给哪个改
                detailEntity.setPurchaseId(finalPurchaseId);
                detailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode());
                return detailEntity;
            }).collect(Collectors.toList());
            // 根据传递的实体类集合，按id去更新实体类中有的字段
            purchaseDetailService.updateBatchById(collect);

            // 3.采购需求合并完成后，需要再更新采购单中的信息
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setId(purchaseId);
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.ASSIGNED.getCode());
            purchaseEntity.setUpdateTime(new Date());
            this.updateById(purchaseEntity);
        }else{
            throw new RuntimeException("采购单状态必须为：新建或已分配");
        }

    }

    /**
     * 采购人员通过模拟App领取采购单
     * 将采购单purchase表的状态改为：已领取；且purchase_detail表的状态同步更新状态：正在采购
     * @param ids：采购人员发生一批他今天去采购的采购单purchase_id
     */
    @Override
    public void received(List<Long> ids) {
        // 1.确认当前采购单状态为：0=新建 或 1=已分配，收集到一起
        List<PurchaseEntity> filteredPurchaseEntities = ids.stream().map(id -> {
            PurchaseEntity purchaseEntity = this.getById(id);
            return purchaseEntity;
        }).filter(purchaseEntity -> {
            if (purchaseEntity.getStatus() == WareConstant.PurchaseStatusEnum.CREATED.getCode() ||
                    purchaseEntity.getStatus() == WareConstant.PurchaseStatusEnum.ASSIGNED.getCode()
            ){ return true;}
            return false;
        }).collect(Collectors.toList());

        // 2.更新这批purchaseEntity的状态为：采购中
        List<PurchaseEntity> collect = filteredPurchaseEntities.stream().map(purchaseEntity -> {
            PurchaseEntity newPurchaseEntity = new PurchaseEntity();
            newPurchaseEntity.setId(purchaseEntity.getId());
            newPurchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.RECEIVE.getCode());
            newPurchaseEntity.setUpdateTime(new Date());
            return newPurchaseEntity;
        }).collect(Collectors.toList());
        this.updateBatchById(collect);

        // 3.根据每一个采购单的id，查出这个采购单对应的所有采购需求List<purchase_detail>
        // 每个采购需求的status都改为：采购中
        collect.forEach(purchaseEntity -> {
            List<PurchaseDetailEntity> purchaseDetailEntities = purchaseDetailService.listPurchaseDetailByPurchaseId(purchaseEntity.getId());
            List<PurchaseDetailEntity> collect1 = purchaseDetailEntities.stream().map(purchaseDetailEntity -> {
                PurchaseDetailEntity myDetailEntity = new PurchaseDetailEntity();
                myDetailEntity.setId(purchaseDetailEntity.getId());
                myDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.BUYING.getCode());
                return myDetailEntity;
            }).collect(Collectors.toList());

            // 这种方式直接影响原始对象，不好！上面的方法是创建一个新的对象，浅拷贝到对应原始对象上
//            List<PurchaseDetailEntity> collect1 = purchaseDetailEntities.stream().map(purchaseDetailEntity -> {
//                purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.BUYING.getCode());
//                return purchaseDetailEntity;
//            }).collect(Collectors.toList());
            purchaseDetailService.updateBatchById(collect1);
        });
    }

    /**
     * 采购人员完成采购单
     * @param purchaseDoneVo
     */
    @Override
    public void done(PurchaseDoneVo purchaseDoneVo) {
        // 从vo中获取采购单id
        Long purchaseId = purchaseDoneVo.getId();

       // 从vo中获取items列表，每个item就是：该采购单中的每一个采购需求 purchaseDetail
        List<PurchaseItemDoneVo> items = purchaseDoneVo.getItems();

        // 标志位，一个采购单中，只要有一项purchaseDetailEntity的状态失败，flag就会被改为false，整个单子就是hasError
        Boolean flag = true;

        List<PurchaseDetailEntity> list = new ArrayList<>();
        for (PurchaseItemDoneVo item : items){
            // 经验：在进入if判断前，先new一个detailEntity，用来做浅拷贝
            PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
            if(item.getStatus() == WareConstant.PurchaseDetailStatusEnum.HASERROR.getCode()){
                flag = false;
                // 状态是失败的情况
                detailEntity.setStatus(item.getStatus());
            }else{
                // 2.修改purchase_detail的状态
                detailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.FINISH.getCode());
                // 3.当单个purchase_detail状态是完成的，就去加库存； 给sku_id在ware_id仓库入库数量
                PurchaseDetailEntity detail = purchaseDetailService.getById(item.getItemId());
                wareSkuService.addStock(detail.getSkuId(),detail.getWareId(),detail.getSkuNum());
            }
            detailEntity.setId(item.getItemId());
            list.add(detailEntity);
        }
        purchaseDetailService.updateBatchById(list);

        // 1.修改purchase采购单的状态为已完成
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        if(!flag){
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.HASERROR.getCode());
        }else{
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.FINISH.getCode());
        }
        purchaseEntity.setUpdateTime(new Date());
        purchaseEntity.setId(purchaseId);
        updateById(purchaseEntity);



    }

}