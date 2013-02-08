#pragma once

#include "opencv/cv.h"
#include "opencv/highgui.h"
#include "opencv/cvaux.h"
#include "opencv2/core/internal.hpp"

#define PI 3.1415926
#define GLCM_DIM 16
#define EOHDIM 37
#define LBP_DIM 256
#define TOTAL_DIM (5*EOHDIM + 5*GLCM_DIM +LBP_DIM)

IplImage* cvGetSubImage(IplImage *image, CvRect roi);
IplImage* Resize(IplImage* src, unsigned int width, unsigned int height);