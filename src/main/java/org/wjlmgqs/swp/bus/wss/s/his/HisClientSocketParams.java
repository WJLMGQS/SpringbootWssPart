package org.wjlmgqs.swp.bus.wss.s.his;

import org.wjlmgqs.swp.core.wss.s.WssSessionMsgData;

/**
 * 医保客户端通信壳
 * @author wjlmgqs@sina.com
 * @date 2018/10/25 17:11
 */
public class HisClientSocketParams<T extends HisClientSocketParams > extends WssSessionMsgData<T> {


    /**
     * 通讯版本
     */
    private String versionType = "";

    /**
     * 通讯业务类型，标识请求业务类型
     */
    private String busiType = "";

    /**
     * 诊所同步项目类型(收费项目、诊断、处方、材料、其他等)
     */
    private String itemType;

    /**
     * 同步时间
     */
    private String modifyTime;

    /**
     * 分页页长
     */
    private Integer pageSize;

    /**
     * 分页下标
     */
    private Integer pageIndex;


    /**
     * 处方类型 诊所项目-处方类型同步时需要该参数
     */
    private String medicineType;

    /**
     * 进销存类型
     */
    private String inventoryType;

    /**
     * 发票号
     */
    private String hisInvoiceNumber;


    /**
     * 查询日期
     */
    private String queryDate;

    public String getVersionType() {
        return versionType;
    }

    public HisClientSocketParams setVersionType(String versionType) {
        this.versionType = versionType;
        return this;
    }

    public String getBusiType() {
        return busiType;
    }

    public HisClientSocketParams setBusiType(String busiType) {
        this.busiType = busiType;
        return this;
    }

    public String getItemType() {
        return itemType;
    }

    public HisClientSocketParams setItemType(String itemType) {
        this.itemType = itemType;
        return this;
    }

    public String getModifyTime() {
        return modifyTime;
    }

    public HisClientSocketParams setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
        return this;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public HisClientSocketParams setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public Integer getPageIndex() {
        return pageIndex;
    }

    public HisClientSocketParams setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
        return this;
    }

    public String getMedicineType() {
        return medicineType;
    }

    public HisClientSocketParams setMedicineType(String medicineType) {
        this.medicineType = medicineType;
        return this;
    }

    public String getInventoryType() {
        return inventoryType;
    }

    public HisClientSocketParams<T> setInventoryType(String inventoryType) {
        this.inventoryType = inventoryType;
        return this;
    }

    public String getHisInvoiceNumber() {
        return hisInvoiceNumber;
    }

    public HisClientSocketParams<T> setHisInvoiceNumber(String hisInvoiceNumber) {
        this.hisInvoiceNumber = hisInvoiceNumber;
        return this;
    }

    public String getQueryDate() {
        return queryDate;
    }

    public HisClientSocketParams<T> setQueryDate(String queryDate) {
        this.queryDate = queryDate;
        return this;
    }
}
