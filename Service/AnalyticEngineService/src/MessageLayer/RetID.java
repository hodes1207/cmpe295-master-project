package MessageLayer;

public enum RetID {
      INVALID,  // Received invalid query request
      
      // Query result value type
      TIMEOUT,
      LONG_LIST, // ArrayList<Long>
      DOMAIN_LIST, // ArrayList<Domain>
      CLASS_LIST,  // ArrayList<SecondLevelClass>
      IMGSERV_LIST, // ArrayList<ImgServerInfo>
      BYTES,     // byte[]
      STRING,    // String
      LONG,      // long
      BOOL,       // boolean value
      INT,        // int value
      DOUBLE,      // Double
      MOD_INFO,
      CLS_RES,
      IMG_DIS_LIST,
}
