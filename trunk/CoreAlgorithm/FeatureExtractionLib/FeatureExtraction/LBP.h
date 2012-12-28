#pragma once
#include "OpenCvCommon.h"

int ExtractLBP(IplImage *src, double rec[LBP_DIM])
{
	memset(rec, 0, LBP_DIM*sizeof(double));
	uchar *data =( uchar*)src->imageData;
	int step = src->widthStep;
	int tmp[8] = {0};

	int nRet = 0;
	for (int i=1;i<src->height-1;i++)
		for(int j=1;j<src->width-1;j++)
		{
			int sum=0;

			if(data[(i-1)*step+j-1]>data[i*step+j])
				tmp[0]=1;
			else
				tmp[0]=0;

			if(data[i*step+(j-1)]>data[i*step+j])
				tmp[1]=1;
			else
				tmp[1]=0;

			if(data[(i+1)*step+(j-1)]>data[i*step+j])
				tmp[2]=1;
			else
				tmp[2]=0;

			if (data[(i+1)*step+j]>data[i*step+j])
				tmp[3]=1;
			else
				tmp[3]=0;

			if (data[(i+1)*step+(j+1)]>data[i*step+j])
				tmp[4]=1;
			else
				tmp[4]=0;

			if(data[i*step+(j+1)]>data[i*step+j])
				tmp[5]=1;
			else
				tmp[5]=0;

			if(data[(i-1)*step+(j+1)]>data[i*step+j])
				tmp[6]=1;
			else
				tmp[6]=0;

			if(data[(i-1)*step+j]>data[i*step+j])
				tmp[7]=1;
			else
				tmp[7]=0;

			int res = (tmp[0]*1+tmp[1]*2+tmp[2]*4+tmp[3]*8+tmp[4]*16+tmp[5]*32+tmp[6]*64+tmp[7]*128);

			//if (res < 15 || res >= 240)
			//	continue;

			rec[res]++;
			nRet++;
		}

		return nRet;
}