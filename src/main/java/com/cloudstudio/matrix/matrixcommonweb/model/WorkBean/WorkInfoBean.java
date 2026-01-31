package com.cloudstudio.matrix.matrixcommonweb.model.WorkBean;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @ClassName：WorkInfoBean
 * @Author: matrix
 * @Date: 2026/1/31 13:16
 * @Description:
 */
@Data
public class WorkInfoBean implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String workID;
    private String workTitle;
    private String priority;
    private String workType;
    private String department;
    private String initiator;
    private String creator;
    private String finishOperator;
    private String workStatus;
    private String creatDate;
    private String finishDate;
    private String workContent;
    private String workResult;
    private String workAssignee;
}
