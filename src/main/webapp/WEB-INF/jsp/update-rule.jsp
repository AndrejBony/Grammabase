<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<html>
    <body>
        <c:set var="context" value="${pageContext.request.contextPath}" />
        <div class="container">    
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h3 class="panel-title"><spring:message code="rule"/> • ${rule.id}</h3>                   
                </div>
                <div class="panel-body">
                    <form name="saveGrammar" class="validate" method="post" action="${context}/save-rule">
                        <form action="${context}/create-grammar" class="form-horizontal validate" id="createGrammar" method="post" >
                            <input type="hidden" name="grammarId" value="${rule.grammarId}">
                            <input type="hidden" name="id" value="${rule.id}">
                            <div class="control-group">
                                <label class="control-label" for="content"><spring:message code="r.con"/></label>
                                <div class="controls">
                                    <textarea name="content" id="content" class="form-control"  style="margin-bottom: 20px;  width: 100%; resize: none">${rule.content}</textarea>
                                </div>
                            </div>
                            <button type="submit" class="btn pull-left btn-primary"><spring:message code="save"/></button>
                            <a type="button" class="btn btn-default" href="${context}/grammar/${rule.grammarId}"><spring:message code="close"/></a>
                        </form>   
                </div>
            </div>
        </div>
    </body>
</html>
