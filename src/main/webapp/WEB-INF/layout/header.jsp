<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    </head>
    <body>       
        <nav class="navbar navbar-inverse navbar-static-top">
            <div class="container">
                <div class="container-fluid">
                    <div class="navbar-header">
                        <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
                        </button>
                        <a class="navbar-brand">Grammabase</a>
                    </div>
                    <div id="navbar" class="navbar-collapse collapse">
                        <ul class="nav navbar-nav">
                            <li><a href='<spring:url value="/" />'><spring:message code="home"/></a></li>
                        </ul>

                    </div>         
                </div>
            </div>
        </nav>
    </body>
</html>
