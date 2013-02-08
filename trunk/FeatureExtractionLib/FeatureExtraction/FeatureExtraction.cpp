// FeatureExtraction.cpp : Defines the exported functions for the DLL application.
//

#include "stdafx.h"
#include "featureMerge.h"
#include "FeatureExtraction.h"

JNIEXPORT void JNICALL Java_imgproc_ImgFeatureExtractionWrapper_extractFeature
  (JNIEnv *jenv, jclass, jbyteArray a, jdoubleArray b)
{
	jdouble* dbBody = jenv->GetDoubleArrayElements(b, 0);

	int nLen = (int)jenv->GetArrayLength(b);
	if (nLen != TOTAL_DIM)
		return;

	jbyte* arrayBody = jenv->GetByteArrayElements(a,0); 
	nLen = (int)jenv->GetArrayLength(a); 
	char* p = (char*)arrayBody; 

	CvMat tmp = cvMat(1, nLen, CV_8UC1, p);
	IplImage* src = cvDecodeImage(&tmp);
	
	if (NULL == src)
		return;

	IplImage* pImgGray = cvCreateImage(cvGetSize(src), 8, 1);
	cvCvtColor(src, pImgGray, CV_BGR2GRAY);

	ExtractFeatureVectors(pImgGray, dbBody);

	jenv->SetDoubleArrayRegion(b, 0, TOTAL_DIM, dbBody);

	//deallocate
	cvReleaseImage(&src);
	cvReleaseImage(&pImgGray);

	jenv->ReleaseByteArrayElements(a, arrayBody, 0);
	jenv->ReleaseDoubleArrayElements(b, dbBody, 0);
}