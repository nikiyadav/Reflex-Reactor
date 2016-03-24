package rr.reflexreactor;

import java.io.Serializable;

public class WifiTransferModel implements Serializable {

    private String FileName;
    private Long FileLength;
    private String InetAddress;


    public WifiTransferModel(){
        //empty constructor
    }

    public WifiTransferModel(String inetaddress) {
        this.InetAddress = inetaddress;
    }

    public WifiTransferModel(String name, Long filelength,String InetAddress){
        this.FileName = name;
        this.FileLength = filelength;
        this.InetAddress=InetAddress;
    }

    public String getInetAddress() {
        return InetAddress;
    }

    public Long getFileLength() {
        return FileLength;
    }

    public String getFileName() {
        return FileName;
    }
}
