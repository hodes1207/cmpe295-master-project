#include "stdafx.h"
#include "OpenCvCommon.h"

IplImage* cvGetSubImage(IplImage *image, CvRect roi)
{
	IplImage *result;
	// set ROI 
	cvSetImageROI(image,roi);

	// create sub image
	result = cvCreateImage( cvSize(roi.width, roi.height), image->depth, image->nChannels );
	cvCopy(image,result);
	cvResetImageROI(image);
	return result;
}

IplImage* Resize(IplImage* src, unsigned int width, unsigned int height)
{
	CvSize sz;
	sz.width = width;
	sz.height = height;

	IplImage* desc = cvCreateImage(sz,src->depth,src->nChannels);
	cvResize(src,desc,CV_INTER_CUBIC);

	return desc;
}