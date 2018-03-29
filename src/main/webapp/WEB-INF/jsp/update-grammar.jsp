<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<html>
    <body>
        <c:set var="context" value="${pageContext.request.contextPath}" />
        <div class="container">    
            <div class="row">
                <div class="panel panel-primary">
                    <div class="panel-heading">
                        <h3 class="panel-title"><spring:message code="grammar"/> • ${grammar.name}</h3>                   
                    </div>
                    <div class="panel-body">
                        <form name="saveGrammar" class="validate" method="post" action="${context}/save-grammar">
                            <input type="hidden" name="id" value="${grammar.id}">
                            <div class="control-group">
                                <label class="control-label" for="name"><spring:message code="g.name"/></label>
                                <div class="controls">
                                    <input type="text" class="form-control" required="true" id="name" name="name" style="margin-bottom: 20px; width: 100%" value="${grammar.name}">
                                    <span class="help-inline"></span>
                                </div>
                            </div>
                            <div class="control-group">
                                <label class="control-label" for="description"><spring:message code="g.des"/></label>
                                <div class="controls">
                                    <textarea name="description" id="description" class="form-control" style="margin-bottom: 20px; width: 100%; resize:none">${grammar.description}</textarea>
                                </div>
                            </div>
                            <div class="control-group">
                                <label class="control-label" for="content"><spring:message code="g.con"/></label>
                                
                                <div class="controls">
                                    <textarea name="content" id="content" class="form-control" style="margin-bottom: 20px;  width: 100%">${grammar.content}</textarea>
                                </div>
                            </div>
                            <button type="submit" class="btn pull-left btn-primary"><spring:message code="save"/></button>
                            <a type="button" class="btn btn-default" href="${context}/"><spring:message code="close"/></a>
                        </form>     
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
