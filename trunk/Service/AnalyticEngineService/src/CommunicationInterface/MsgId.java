package CommunicationInterface;

public enum MsgId {
	
	    UNINIT,     // Uninitialized
	    
	    // Image
        GET_IMAGE,  // Retrieve Image
        DEL_IMAGE,  // Delete Image
        ADD_IMAGE,  // Add Image
        
        // Classification 
        ADD_DOMAIN, // Add Domain
        GET_DOMAIN, // Get Domain, First level classification
        GET_CLASS,  // Second level classification
        
        // Model Tuning
        SET_RBFKP,  // RBF Kernel Param
        SET_LKP,    // Linear Kernel Param
        ENABLE_RBF, // Enable RBF tuning
        DISABLE_RBF,// Disable RBF tuning
        CHECK_RBF,  // Is RBF tuning enabled
        
        // Auto Tuning
        GET_ATP,    // Get auto tuning progress
        GET_ATI,    // Get auto tuning info
        GET_ATFN,   // Get auto tuning fold number
        SET_ATFN,   // Set auto tuning fold number
        GET_CMI,    // Get Current model info
        START_TUNE, // Start auto tuning
        STOP_TUNE,  // Stop auto tuning
        START_TRAIN,// Start Training
        
        // Recommendation API
        SEARCH_SIM, // Similarity search
        GET_CLEST,  // Classification estimation       
        
}
