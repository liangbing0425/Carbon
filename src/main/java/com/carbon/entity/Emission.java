package com.carbon.entity;

import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

public class Emission {
    private int id;
    private int companyId;
    private int status;//0待审核，1通过，2驳回
    private String type;//行业类型（例如：电解铝生产）
    private String proofFile;//证明材料,多张图片以;分隔
    private String content;//报告内容（json格式存储）
    private Date createTime;

    public Date getCreateTime() {
        return createTime;
    }


    public String getProofFile() {
        return proofFile;
    }

    public void setProofFile(String proofFile) {
        this.proofFile = proofFile;
    }
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
