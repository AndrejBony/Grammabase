<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<html>  
    <body>
        <c:set var="context" value="${pageContext.request.contextPath}" />
        <div class="container">
            <form class="form-horizontal validate" id="installForm" method="post" action="${context}/create-database" name="config">
                <label class="control-label" for="inputName"><spring:message code="db.name"/></label>
                <div class="controls">
                    <input type="text" class="form-control" required="true" id="inuptName"  name="inputName" style="width: 50%">
                    <span class="help-inline"></span>
                </div>
                <br>
                <br>
                <div class="control-group">
                    <div class="controls">
                        <button type="submit" class="btn btn-primary"><spring:message code="db.create"/></button>            
                    </div>
                </div>
            </form>
        </div>
    </body>
</html>
