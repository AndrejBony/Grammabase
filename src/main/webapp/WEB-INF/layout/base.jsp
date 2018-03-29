<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title><tiles:getAsString name="title" /></title>
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css">
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap-theme.min.css" >
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.2.0/jquery.min.js"></script>
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js" ></script>
        <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/zeroclipboard/2.2.0/ZeroClipboard.min.js"></script>
        <script src="https://cdn.rawgit.com/Triforcey/clip-j/d4cf52adb248f236331acd8401610fc198a1fb57/clip-j.js"></script>
        <style type="text/css">
            body {
                margin-top: 0px;
                padding-bottom: 40px;
            }
            td {
                vertical-align: middle;
            }
            textarea { 
                padding: 10px;
            }
        </style>
    </head>
    <body>

        <c:set var="context" value="${pageContext.request.contextPath}" />

        <tiles:insertAttribute name="header" />
        <tiles:insertAttribute name="body" />
        <tiles:insertAttribute name="footer" />

        <script>
            var copyTextareaBtn = document.querySelector('.textarea-copybtn');

            copyTextareaBtn.addEventListener('click', function (event) {
                var copyTextarea = document.querySelector('.copy-textarea');
                copyTextarea.select();
                //document.execCommand('copy');     
            });
        </script>       
        <script>
            function h(e) {
                $(e).css({'height': 'auto', 'overflow-y': 'hidden'}).height(e.scrollHeight);
            }
            $('textarea').each(function () {
                h(this);
            }).on('input', function () {
                h(this);
            });
        </script>
    </body>
</html>