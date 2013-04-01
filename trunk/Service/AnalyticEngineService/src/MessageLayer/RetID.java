package MessageLayer;

public enum RetID {
      INVALID,  // Received invalid query request
      
      // Query result value type
      LONG_LIST, // ArrayList<Long>
      DOMAIN_LIST, // ArrayList<Domain>
      CLASS_LIST,  // ArrayList<SecondLevelClass>
      BYTES,     // byte[]
      STRING,    // String
      LONG,      // long
      BOOL,       // boolean value
      INT,        // int value
      DOUBLE,      // Double
      MOD_INFO
}
