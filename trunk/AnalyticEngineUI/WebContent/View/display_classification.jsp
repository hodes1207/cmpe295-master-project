<%
String result = (String)session.getAttribute("ClassificationResult");

if(result == "")
{}
else{
%>

<table class="uploaded_photo">
<tr align=center>
<td height=100 align=center valign=middle width=900 colspan=2>
Input Image <br/>
<a href="javascript:open_uploaded_pic_window();"> <img src="show_uploadedphoto" height="100" width="100" alt="The uploaded photo..." title="The uploaded photo..."></a> 
</td>

</tr>
</table>


<%

String[] strs = result.split("\n");


for(int i = 0; i < strs.length; i++)
{
	System.out.println(strs[i]);
    out.println(strs[i]+"<br/>");


}
}
%>