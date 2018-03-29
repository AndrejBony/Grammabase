<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<html>
    <body>
        <c:set var="context" value="${pageContext.request.contextPath}" />
        <div class="container">    
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h3 class="panel-title"><spring:message code="grammar"/></h3>                   
                </div>
                <div class="panel-body">
                    <form action="${context}/create-grammar" class="form-horizontal validate" id="createGrammar" method="post" >
                        <div class="control-group">
                            <label class="control-label" for="name"><spring:message code="g.name"/></label>
                            <div class="controls">
                                <input type="text" class="form-control" required="true" id="inputName" name="inputName" style="margin-bottom: 20px; width: 100%">
                                <span class="help-inline"></span>
                            </div>
                        </div>
                        <div class="control-group">
                            <label class="control-label" for="description"><spring:message code="g.des"/></label>
                            <div class="controls">
                                <textarea name="inputDes" id="inputDes" class="form-control" style="margin-bottom: 20px;  width: 100%; resize:none"></textarea>
                            </div>
                        </div>
                        <button type="submit" class="btn pull-left btn-primary"><spring:message code="save"/></button>
                        <a type="button" class="btn btn-default" href="${context}/"><spring:message code="close"/></a>
                    </form>   
                </div>
            </div>
        </div>
    </body>
</html>
