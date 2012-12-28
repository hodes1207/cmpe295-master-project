#pragma once
#include "EOHextract.h"
#include "LBP.h"
#include "GLCM.h"
#include <algorithm>
#include <vector>
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/imgproc/imgproc.hpp"
using namespace std;

//////////////////////////////////////////////////////////////////////////

void ExtractFeatureVectors(IplImage* src, double features[TOTAL_DIM])
{
	if (NULL == src)
		return;
	
	IplImage* pReSizedImg= Resize(src, 100, 100);
	
	ExtractEOHAll(pReSizedImg, features);
	GetGLCMdescsAll(pReSizedImg, features+5*EOHDIM);
	ExtractLBP(pReSizedImg, features+5*EOHDIM + 5*GLCM_DIM);

	cvReleaseImage(&pReSizedImg);
}