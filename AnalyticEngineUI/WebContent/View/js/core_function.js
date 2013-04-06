function open_pic_window( pic_id)
{
	mywin = window.open(
			"/AnalyticEngineUI/showphoto?imgId=" + pic_id,
			"Large Image",
			"toolbar=no,location=no,directories=no,scrollbars=no,status=no,menubar=no,resizable=no,width=400,height=400");
	mywin.focus();
}

function open_uploaded_pic_window()
{
	mywin = window.open(
			"/AnalyticEngineUI/show_uploadedphoto",
			"Large Image",
			"toolbar=no,location=no,directories=no,scrollbars=no,status=no,menubar=no,resizable=no,width=400,height=400");
	mywin.focus();
}