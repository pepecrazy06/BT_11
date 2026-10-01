<%@ page contentType="text/html;charset=UTF-8" %>

<!DOCTYPE html>
<html lang="vi">

<head>

    <meta charset="UTF-8">

    <title>
        Quản trị - <sitemesh:write property="title"/>
    </title>

    <sitemesh:write property="head"/>

</head>

<body>

    <div class="sitemesh-admin-layout">

        <sitemesh:write property="body"/>

    </div>

</body>

</html>