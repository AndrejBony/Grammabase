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
                        <h5 class="panel-title"><spring:message code="grammar"/> • <a href="${context}/grammar/${grammar.id}">${grammar.name}</a></h5>
                    </div>
                    <div class="panel-body" style="margin: 10px 10px 20px 10px;">
                        <h4><spring:message code="g.des"/></h4>
                        <div class="row">
                            <div class="span12">
                                <div class="well">
                                    <c:choose>
                                        <c:when test="${empty grammar.description}"><spring:message code="g.no_des"/></c:when>
                                        <c:otherwise>${grammar.description}</c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="panel panel-default">
                                <div class="panel-heading">
                                    <h4 class="panel-title"><spring:message code="rules"/></h4>
                                </div>
                                <div class="panel-body">
                                    <div class="span4">
                                        <form name="ruleAdd" method="post" action="${context}/create-rule" class="form-inline pull-left" style="margin: 15px 0 0 50px">
                                            <div class="input-append">
                                                <input type="hidden" name="grammarId" value="${grammar.id}">
                                                <input type="text" name="ruleId" placeholder="<spring:message code="r.name"/>">
                                                <button type="submit" class="btn btn-sm btn-default"><spring:message code="r.new"/></button>
                                            </div>
                                        </form>
                                    </div>
                                    <div class="span4">
                                        <form name="ruleSearch" method="GET" class="form-inline pull-right" style="margin: 15px 50px 0 0 ">
                                            <div class="input-append">
                                                <input type="hidden" name="search" value="true">
                                                <input type="text" name="name" placeholder="<spring:message code="rule"/>" <c:if test="search == true"> value="${searchString}"</c:if>>
                                                <button type="submit" class="btn btn-sm btn-default"><spring:message code="search"/></button>
                                            </div>
                                        </form>
                                    </div>
                                    <table class="table table-hover">
                                        <thead>
                                        <div class="row">
                                            <tr>
                                                <th><spring:message code="r.id"/></th>
                                                <th></th>
                                            </tr>
                                            </thead>
                                            <tbody> 
                                                <c:choose>
                                                    <c:when test="${empty rules}">
                                                    <td>
                                                        <h4 style="color: crimson"><spring:message code="no_rules"/></h4>
                                                    </td>
                                                </c:when>
                                                <c:otherwise>
                                                    <c:forEach var="rule" items="${rules}">
                                                        <tr>
                                                            <td>${rule.id}</td>
                                                            <td>
                                                                <div class="pull-right">
                                                                    <a class="btn btn-sm btn-info" href="${context}/grammar/${rule.grammarId}/rule/${rule.id}"><spring:message code="update"/></a>
                                                                    <a class="btn btn-sm btn-primary" href="${context}/delete-rule/${rule.grammarId}/${rule.id}"><spring:message code="delete"/></a>
                                                                </div>
                                                            </td>
                                                        </tr>
                                                    </c:forEach>
                                                </c:otherwise>
                                            </c:choose>
                                            </tbody>
                                    </table>         
                                </div>
                            </div>
                        </div>
                        <div class ="row"> 
                            <button type="button" class="btn btn-primary" data-toggle="modal" data-target="#myModal" style="margin-bottom: 20px">
                                <spring:message code="show_xml"/>
                            </button>
                        </div>
                        <!-- Modal -->
                        <div class="modal fade bs-example-modal-lg" id="myModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel">
                            <div class="modal-dialog modal-lg" role="document">
                                <div class="modal-content">
                                    <div class="modal-header">
                                        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                                        <h4 class="modal-title" id="exampleModalLabel"><spring:message code="grammar"/>: ${grammar.name}</h4>
                                    </div>
                                    <div class="modal-body">
                                        <form>                                           
                                            <div class="form-group">                                
                                                <label for="xml-content" class="control-label"><spring:message code="g.xml"/></label>
                                                <textarea name="xml-content" id="xml-content" class="form-control copy-textarea" rows="4" style="margin-bottom: 20px;  width: 100%">${xml}</textarea>                                               
                                            </div>                                          
                                        </form>
                                    </div>
                                    <div class="modal-footer"> 
                                        <button class="btn btn-primary textarea-copybtn"><spring:message code="copy"/></button>
                                        <button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="close"/></button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                                                
                </div>
            </div>
        </div>
    </body>
</html>
