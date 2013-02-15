package encryptor.main;

import java.io.IOException;
import java.util.EventListener;

public interface IencryptorListener extends EventListener {
  public void encryptRequest(EncryptEvent event) throws  IOException;
  public void decryptRequest(EncryptEvent event) throws  IOException;
}
