package thirdpart.bean;

import lombok.*;
import thirdpart.order.OrderCmd;

import java.io.Serializable;
import java.util.List;

/**
 * @BelongsProject: seq
 * @BelongsPackage: thirdpart.bean
 * @Author: Alone
 * @CreateTime: 2020-11-09 14:41
 * @Description: 发送到撮合核心的数据包的结构
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CmdPack implements Serializable {

    //包号
    private long packNo;

    //委托数据
    private List<OrderCmd> orderCmds;
}
