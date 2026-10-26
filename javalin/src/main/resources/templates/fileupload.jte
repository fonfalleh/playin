@import io.github.fonfalleh.javalin.fileupload.Fields;


<head>
    <title>File upload</title>
    <link href="https://fonts.googleapis.com/css?family=Cherry+Swash|Raleway" rel="stylesheet">
    <link href="style.css" rel="stylesheet">
</head>
<body>
<p>Upload musescore files to be indexed, with the possibility to supply additional metadata.</p>
<form method="post" action="/fileupload" enctype="multipart/form-data">

    <label for="file">File to upload</label><br>
    <input type="file" id="file" name="file"><br>


@for(Fields field: Fields.values())
    <label for="${field.getName()}">${field.getName()}</label><br>
    <input type="text" name="${field.getName()}" id="${field.getName()}"><br>
@endfor

    <label for="freetext">freetext</label>
    <textarea id="freetext" name="freetext" rows="5" cols="80"></textarea> <!-- TODO? This is ignored now. Perhaps prepopulate with fields? -->

    <button>Submit</button>
</form>
</body>
