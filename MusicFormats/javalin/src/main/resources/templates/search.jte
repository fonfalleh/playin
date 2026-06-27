@import gg.jte.Content
@import io.github.fonfalleh.javalin.search.FacetOption
@import io.github.fonfalleh.javalin.search.FieldFacet

@param Content documentList
@param FieldFacet composerFacet
@param String query

<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Playin'!</title>
    <link href="https://fonts.googleapis.com/css?family=Cherry+Swash|Raleway" rel="stylesheet">
    <link href="style.css" rel="stylesheet">
</head>

<body>
<header>
    <h1>Playin'!</h1>
</header>
<div>
    <aside>
        <form method="get" action="/">
            <div>
                <label for="composer">Choose a composer:</label>
                <select name="composer" id="composer">
                    @for(FacetOption option: composerFacet.options())
                        <option value="${option.name()}"
                                selected="${option.selected()}"
                        >${option.name()}@if(!option.name().equals("All")) (${option.count()})@endif</option>
                    @endfor
                </select>
            </div>
            <div>
                <label for="searchBox">Enter search term:</label>
                <input name="query" type="text" id="searchBox" placeholder="e.g. bach" value="${query}">
            </div>
            <div>
                <button>Filter results</button>
            </div>
        </form>
    </aside>
    <main>
        ${documentList}
    </main>
</div>
<footer>
    <p>Website design stolen from <a
                href="https://github.com/mdn/learning-area/tree/main/javascript/apis/fetching-data/can-store">MDN can
            store example</a></p>
</footer>
</body>
</html>
