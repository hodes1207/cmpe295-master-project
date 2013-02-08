#include <stdio.h>
#include <ctype.h>
#include "OpenCvCommon.h"

void ExtractEOH(IplImage* src, double eoh[EOHDIM]);
void ExtractEOHAll(IplImage* img, double eoh[5*EOHDIM])
{
	ExtractEOH(img, eoh);

	//top left
	CvRect rect1;
	rect1.x = 0; 
	rect1.y = 0; 
	rect1.width = img->width/2; 
	rect1.height = img->height/2;

	IplImage* pImg1 = cvGetSubImage(img, rect1);
	ExtractEOH(pImg1, eoh+EOHDIM);

	//top right
	CvRect rect2;
	rect2.x = img->width/2; 
	rect2.y = 0; 
	rect2.width = img->width/2; 
	rect2.height = img->height/2;

	IplImage* pImg2 = cvGetSubImage(img, rect2);
	ExtractEOH(pImg2, eoh+2*EOHDIM);

	//bottom left
	CvRect rect3;
	rect3.x = 0; 
	rect3.y = img->height/2; 
	rect3.width = img->width/2; 
	rect3.height = img->height/2;

	IplImage* pImg3 = cvGetSubImage(img, rect3);
	ExtractEOH(pImg3, eoh+3*EOHDIM);

	//bottom right
	CvRect rect4;
	rect4.x = img->width/2; 
	rect4.y = img->height/2; 
	rect4.width = img->width/2; 
	rect4.height = img->height/2;

	IplImage* pImg4 = cvGetSubImage(img, rect4);
	ExtractEOH(pImg4, eoh+4*EOHDIM);

	cvReleaseImage(&pImg4);
	cvReleaseImage(&pImg3);
	cvReleaseImage(&pImg2);
	cvReleaseImage(&pImg1);
}

void ExtractEOH(IplImage* src, double eoh[EOHDIM])
{
	CvHistogram *hist = 0; // define multi_demention histogram
	IplImage* canny;
	CvMat* canny_m;
	IplImage* dx; // the sobel x difference 
	IplImage* dy; // the sobel y difference 
	CvMat* gradient; // value of gradient
	CvMat* gradient_dir; // direction of gradient
	CvMat* dx_m; // format transform to matrix
	CvMat* dy_m;
	CvMat* mask;
	CvSize  size;
	IplImage* gradient_im;
	int i,j;
	float theta;

	int hdims = EOHDIM;     
	float hranges_arr[] = {-PI/2,PI/2}; 
	float* hranges = hranges_arr;

	float max_val;  // 
	int bin_w;

	size=cvGetSize(src);
	canny=cvCreateImage(cvGetSize(src),8,1);
	dx=cvCreateImage(cvGetSize(src),32,1);
	dy=cvCreateImage(cvGetSize(src),32,1);
	gradient_im=cvCreateImage(cvGetSize(src),32,1);
	canny_m=cvCreateMat(size.height,size.width,CV_32FC1);
	dx_m=cvCreateMat(size.height,size.width,CV_32FC1);
	dy_m=cvCreateMat(size.height,size.width,CV_32FC1);
	gradient=cvCreateMat(size.height,size.width,CV_32FC1);
	gradient_dir=cvCreateMat(size.height,size.width,CV_32FC1);
	mask=cvCreateMat(size.height,size.width,CV_32FC1);
	
	cvCanny(src,canny,60,180,3);

	cvConvert(canny,canny_m);

	cvSobel(src,dx,1,0,3);
	cvSobel(src,dy,0,1,3);

	cvConvert(dx,dx_m);

	cvConvert(dy,dy_m);
	cvAdd(dx_m,dy_m,gradient); // value of gradient
	cvDiv(dx_m,dy_m,gradient_dir); // direction

	for(i=0;i<size.height;i++)
		for(j=0;j<size.width;j++)
		{
			if(cvmGet(canny_m,i,j)!=0 && cvmGet(dx_m,i,j)!=0)
			{
				theta=cvmGet(gradient_dir,i,j);
				theta=atan(theta);
				cvmSet(gradient_dir,i,j,theta);  
			}
			else
			{
				cvmSet(gradient_dir,i,j,0);
			}
		}

		hist = cvCreateHist( 1, &hdims, CV_HIST_ARRAY, &hranges, 1 );  

		cvConvert(gradient_dir,gradient_im);
		cvCalcHist( &gradient_im, hist, 0, canny ); 
		cvGetMinMaxHistValue( hist, 0, &max_val, 0, 0 );  
		cvConvertScale( hist->bins, hist->bins, max_val ? 255. / max_val : 0., 0 ); 

		for( i = 0; i < hdims; i++ )
		{
			double val = cvGetReal1D(hist->bins,i);
			eoh[i] = val;
		}

		cvReleaseHist(&hist);
		cvReleaseImage(&canny);
		cvReleaseImage(&dx);
		cvReleaseImage(&dy);cvReleaseImage(&gradient_im);
		cvReleaseMat(&canny_m);
		cvReleaseMat(&dx_m);
		cvReleaseMat(&dy_m);
		cvReleaseMat(&gradient);
		cvReleaseMat(&gradient_dir);
		cvReleaseMat(&mask);
}