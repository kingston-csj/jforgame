import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;

@ProtobufClass
public class ReqAccountLogin {

    private long accountId;

    private String password;

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
