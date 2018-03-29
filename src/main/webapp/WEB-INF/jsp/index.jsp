<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<html>
    <body>
        <c:set var="context" value="${pageContext.request.contextPath}" />
        <div class="container">
            <div class="row">
                <div class="panel panel-primary">
                    <div class="panel-heading">
                        <h3 class="panel-title"><spring:message code="grammars"/></h3>
                    </div>
                    <div class="panel-body">

                        <table class="table table-hover">
                            <thead>
                            <div class="row">
                                <tr>
                                    <th><spring:message code="g.name"/></th>
                                    <th>
                                        <div class="pull-right">
                                            <a href="${context}/add-grammar" method="POST" class="btn btn-default"><spring:message code="g.create"/></a>
                                        </div>
                                    </th>
                                </tr>
                                </thead>
                                <tbody>
                                    <c:choose>
                                        <c:when test="${empty grammars}">
                                        <td>
                                             <h4 style="color: crimson"><spring:message code="no_grammars"/></h4>
                                        </td>
                                    </c:when>
                                    <c:otherwise>
                                        <c:forEach var="grammar" items="${grammars}">
                                            <tr>
                                                <td><a href="${context}/grammar/${grammar.id}">${grammar.name}</a></td>
                                                <td>
                                                    <div class="pull-right">
                                                        <a class="btn btn-sm btn-info" href="${context}/update-grammar/${grammar.id}"><spring:message code="update"/></a>
                                                        <a class="btn btn-sm btn-primary" href="${context}/delete-grammar/${grammar.id}"><spring:message code="delete"/></a>
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
        </div>
    </body>
</html>
