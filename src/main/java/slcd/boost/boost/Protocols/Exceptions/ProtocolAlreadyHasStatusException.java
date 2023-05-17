package slcd.boost.boost.Protocols.Exceptions;

import lombok.Data;

@Data
public class ProtocolAlreadyHasStatusException extends RuntimeException{

    private static final String EXCEPTION_NAME = "PROTOCOL_ALREADY_HAS_STATUS";
    private String statusName;
    private String protocolUuid;
    public ProtocolAlreadyHasStatusException(String statusName, String protocolUuid){
        this.statusName = statusName;
        this.protocolUuid = protocolUuid;
    }
}
