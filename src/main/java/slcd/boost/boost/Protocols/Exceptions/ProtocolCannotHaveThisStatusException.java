package slcd.boost.boost.Protocols.Exceptions;

import lombok.Data;

@Data
public class ProtocolCannotHaveThisStatusException extends RuntimeException{

    private static final String EXCEPTION_NAME = "PROTOCOL_CANNOT_HAVE_THIS_STATUS";
    private String statusName;
    private String protocolUuid;

    private String currentStatus;
    public ProtocolCannotHaveThisStatusException(String statusName, String protocolUuid, String currentStatus){
        this.statusName = statusName;
        this.protocolUuid = protocolUuid;
        this.currentStatus = currentStatus;
    }
}
