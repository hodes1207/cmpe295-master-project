package MessageLayer;

public enum MsgId {
	
    UNINIT,     // Uninitialized
    
    // Image
    GET_IMAGE,  // Retrieve Image
    DEL_IMAGE,  // Delete Image
    ADD_IMAGE,  // Add Image
    GET_PICID,  // Get pic id
    
    // Classification 
    ADD_DOMAIN, // Add Domain
    GET_DOMAIN, // Get Domain, First level classification
    GET_CLASS,  // Second level classification
    
    // Model Tuning & Training
    SET_RBFKP,  // RBF Kernel Param
    SET_LKP,    // Linear Kernel Param
    ENABLE_RBF, // Enable RBF tuning
    DISABLE_RBF,// Disable RBF tuning
    CHECK_RBF,  // Is RBF tuning enabled
    GET_ATP,    // Get auto tuning progress
    GET_ATI,    // Get auto tuning info
    GET_ATFN,   // Get auto tuning fold number
    SET_ATFN,   // Set auto tuning fold number
    GET_CMI,    // Get Current model info
    GET_PERF_INFO, // Get image server performance information
    START_TUNE, // Start auto tuning
    STOP_TUNE,  // Stop auto tuning
    START_TRAIN,// Start Training
    
    GET_IMGSERV, //get image server information
    GET_MODEL_ACCURACY, //get the accuracy of a specific model
    GET_MODEL_TUNINGINFO, // get the tuning information of a specific model
    GET_MODEL_TRAININGINFO, // if the training is in progress for a specific model
    
    // Recommendation API
    SEARCH_SIM, // Similarity search
    GET_CLEST,  // Classification estimation  
}
