package CommunicationInterface;

public enum RetID {
      INVALID,  // Received invalid query request
      
      // Query result value type
      LONG_LIST, // ArrayList<Long>
      DOMAIN_LIST, // ArrayList<Domain>
      BYTES,     // byte[]
      STRING,    // String
      LONG       // long
}
