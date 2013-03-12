<HTML>
<HEAD>
<TITLE>Test </TITLE>

</HEAD>

<BODY>
<TABLE>
<TR>
<TD>Image Testing</TD>
</TR>
<TR>
<TD>
<%
int imgId = (1<<16)+1;
%>
<img src="test2.jsp?imgId=<%=imgId%>"></TD>
</TR>
</TABLE>

</BODY>
</HTML>