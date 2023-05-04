package org.dromara.hodor.admin.domain;

import cn.hutool.json.JSONUtil;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import org.dromara.hodor.admin.dto.DependenceDataDTO;

/**
 * @author tomgs
 * 
 * @since 1.0
 **/
@Data
public class JobDependencies implements Serializable {

    private Integer id;

    private String groupName;

    private String jobName;

    private Date createTime;

    private Date updateTime;

    private int encType;

    private byte[] dependenceData;

    public DependenceDataDTO getDependenceDataObj() {
        DependenceDataDTO dto = null;
        try {
            if (dependenceData != null && dependenceData.length > 0) {

                ByteArrayInputStream in = new ByteArrayInputStream(dependenceData);
                ObjectInputStream sIn = new ObjectInputStream(in);

                String dtoString = sIn.readObject().toString();//FileUtils.getUnzipObject(dependenceData);
                dto = JSONUtil.toBean(dtoString, DependenceDataDTO.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return dto;
    }

    public void setDependenceDataObj(DependenceDataDTO dependenceData) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream sOut = null;
        try {
            sOut = new ObjectOutputStream(out);
            sOut.writeObject(JSONUtil.toJsonStr(dependenceData));
            sOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] bytes = out.toByteArray();

        this.dependenceData = bytes;//FileUtils.getzipBytes(dependenceData);
    }

    public byte[] getDependenceData() {
        return dependenceData;
    }

    public void setDependenceData(byte[] dependenceData) {
        this.dependenceData = dependenceData;
    }
}
