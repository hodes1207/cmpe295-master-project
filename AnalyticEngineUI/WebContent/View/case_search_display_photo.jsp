<script type="text/javascript" src="/AnalyticEngineUI/View/js/core_function.js" ></script>


<%
int prvPagStartId, fastBackPagStartId, firstPagStartId, 
    nextPagStartId, fastFwdPagStartId, lastPagStartId, currentPagStartId, pageNumber, totalPageNumber;
int imgNumber_EachPage = 10;


currentPagStartId = (Integer)session.getAttribute("page_start_id");

if(currentPagStartId == -1)
{}
else{


	Long[] imgIdAry = (Long[])session.getAttribute("imgIdAry");

//calculate total page number
int tmp = imgIdAry.length/imgNumber_EachPage;
if(imgIdAry.length%imgNumber_EachPage == 0)
	totalPageNumber = tmp;
else
	totalPageNumber = tmp + 1;

if(currentPagStartId == 0)
{
	prvPagStartId = 0;
	fastBackPagStartId = 0;
}
else
{
	prvPagStartId = currentPagStartId-imgNumber_EachPage;
	//validate fastBackPagStartId
	tmp = currentPagStartId - 10 * imgNumber_EachPage;
			if(tmp > 0)
				fastBackPagStartId = tmp;
			else
	            fastBackPagStartId = 0;	
}
firstPagStartId = 0;
nextPagStartId = currentPagStartId + imgNumber_EachPage;
lastPagStartId = (totalPageNumber - 1) * imgNumber_EachPage;
tmp = currentPagStartId + imgNumber_EachPage * 10;  // forward next ten pages

//validate fastFwdPagStartId
if(tmp <= imgIdAry.length)
	fastFwdPagStartId = tmp;
else
	fastFwdPagStartId = lastPagStartId;



%>

<table class="uploaded_photo">
<tr align=center>
<td height=100 align=center valign=middle width=900 colspan=2>
Input Image <br/>
<a href="javascript:open_uploaded_pic_window();"> <img src="show_uploadedphoto" height="100" width="100" alt="The uploaded photo..." title="The uploaded photo..."></a> 
</td>

</tr>
</table>

<TABLE class="paginator" >

<TR align=center>
	<td width=30><img src="View/images/leer.gif" width=30 height=1></td>
	
	<td align=center width=20 height=24>			
	<form id="first_post_form" action="case_search_photo_frame" method="post" target="case_search_photo_frame">	
	<input type="image" name="demo_thumbs_button_first" src="View/images/button_first.gif" alt="Jump to the beginning of the list..." title="Jump to the beginning of the list..."
							 onclick="document.getElementById('first_post_form').submit()"		>
	<input type="hidden" name="demo_thumbs_button_first_x">		
    <input type="hidden" name="pageFunctionCall" value="first_post">			
    <input type="hidden" name="demo_thumbs_offset_first_post" value="<%=firstPagStartId%>">
    </form>
    </td>
  
  <td width=8><img src="View/images/leer.gif" width=8 height=1></td>
  
  <td align=center width=20 height=24>			
	<form id="fast_back_post_form" action="case_search_photo_frame" method="post" target="case_search_photo_frame">	
  <input type="image" src="View/images/button_fast_back.gif" alt="Ten steps backward..." title="Ten steps backward..." 
  onclick="document.getElementById('fast_back_post_form').submit()">
  <input type="hidden" name="demo_thumbs_button_fast_back_x">
  <input type="hidden" name="pageFunctionCall" value="fast_back_post">			
  <input type="hidden" name="demo_thumbs_offset_fast_back_post" value="<%=fastBackPagStartId %>"></form></td>
  
  <td width=8><img src="View/images/leer.gif" width=8 height=1></td>
  
  <td align=center width=20 height=24>			
	<form id="prev_post_form" action="case_search_photo_frame" method="post" target="case_search_photo_frame">	
  <input type="image" name="demo_thumbs_button_back" src="View/images/button_back.gif" alt="One step backward..." title="One step backward..."
							 onclick="document.getElementById('prev_post_form').submit()"		>
  <input type="hidden" name="demo_thumbs_button_back_x">			
  <input type="hidden" name="pageFunctionCall" value="prev_post">			
  <input type="hidden" name="demo_thumbs_offset_prev_post" value="<%=prvPagStartId %>"></form></td>

  <td width=30><img src="View/images/leer.gif" width=20 height=1></td>
        
  <td align=center width="100%">
  <b>Retrieved Images: <%=imgIdAry.length %>
         (<%=currentPagStartId %>-<%=currentPagStartId+imgNumber_EachPage %>)                  
  <input type="hidden" name="demo_thumbs_offset_current_post" value="13"></b>
  </td>


  <td align=center width=20 height=24>		
	<form id="next_post_form" action="case_search_photo_frame" method="post" target="case_search_photo_frame">	
  <input type="hidden" name="demo_thumbs_button_next_x">			
  <input type="hidden" name="pageFunctionCall" value="next_post">			
  <input type="hidden" name="demo_thumbs_offset_next_post" value="<%=nextPagStartId %>">
    <input type="image" name="demo_thumbs_button_next" src="View/images/button_next.gif" alt="One step forward..." title="One step forward..."
							 onclick="document.getElementById('next_post_form').submit()"		>
  </form></td>

  <td width=8><img src="View/images/leer.gif" width=8 height=1></td>

  <td align=center width=20 height=24>			
	<form id="fast_next_post_form" action="case_search_photo_frame" method="post" target="case_search_photo_frame">	
  <input type="image" name="demo_thumbs_button_fast_next" src="View/images/button_fast_next.gif" alt="Ten steps forward..." title="Ten steps forward..."
							 onclick="document.getElementById('fast_next_post_form').submit()"		>
  <input type="hidden" name="demo_thumbs_button_fast_next_x">
  <input type="hidden" name="pageFunctionCall" value="fast_next_post">			
  <input type="hidden" name="demo_thumbs_offset_fast_next_post" value="<%=fastFwdPagStartId%>"></form></td>

  <td width=8><img src="View/images/leer.gif" width=8 height=1></td>

  <td align=center width=20 height=24>			
	<form id="last_post_form" action="case_search_photo_frame" method="post" target="case_search_photo_frame">	
  <input type="image" name="demo_thumbs_button_last" src="View/images/button_last.gif" alt="Jump to the end of the list..." title="Jump to the end of the list..."
							 onclick="document.getElementById('last_post_form').submit()"		>
  <input type="hidden" name="demo_thumbs_button_last_x">		
  <input type="hidden" name="pageFunctionCall" value="last_post">			
  <input type="hidden" name="demo_thumbs_offset_last_post" value="<%=lastPagStartId %>"></form></td>

  <td width=30><img src="View/images/leer.gif" width=30 height=1></td>
</TR>
</TABLE>

<table class="imageBrowse" id="imageBrowse" align=center>
<%
for(int i = 0; i < 2; i++)
{

%>
	<tr align=center>
	
	<% 
	for(int j = 0; j < 5; j++)
	{
		int nid= currentPagStartId+(i*5)+j;
		if( nid< imgIdAry.length)
		{
	   long imgId = imgIdAry[nid];
	%>
	<td width=140 height=140 align=center valign=middle>
	
<table  class="single_img_table">
<tbody>
<tr>
<td align=center valign=middle colspan=2 style="font-weight:bold;font-size: 8pt;font-family: tahoma, arial,verdana, helvetica, sans-serif;color: rgb(231, 121, 121);"><%=imgId%> </td>
</tr>

<tr>
<td height=100 align=center valign=middle width=130 colspan=2>
<a href="javascript:open_pic_window(<%=imgId%>);"> <img src="showphoto?imgId=<%=imgId%>" height="100" width="100"></a> 
</td>
</tr>

<tr>
<td align=center valign=middle colspan=2 style="font-weight:bold;font-size: 8pt;font-family: tahoma, arial,verdana, helvetica, sans-serif;color: rgb(231, 121, 121);"><%=imgId%> </td>
</tr>
</tbody>
</table>


</td>
<%
		}
		else{}
}
%>
</tr>
<%
}
%>
</table>

<%
}
%>
