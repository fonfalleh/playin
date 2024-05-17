/** TODO restructure, refactor, and rename things.
Also, filtering is not clientside

Basically, check params *1, construct solr query from params
display every matching song that is returned
display facets, make an entry selected if params are there
  if selecting a filter/facet, update params
if searching, add to params, refresh?


https://www.sitepoint.com/get-url-parameters-with-javascript/
TLDR:
```
const queryString = window.location.search;
console.log(queryString);
// ?product=shirt&color=blue&newuser&size=m

const urlParams = new URLSearchParams(queryString);

const product = urlParams.get('product')
console.log(product);
// shirt

console.log(urlParams.has('product'));
// true

console.log(urlParams.getAll('size'));
// [ 'm' ]
```

*/

//TODO actual query. Go 100% json query? json facet responses are structured nicely, and it could be weird to mix payload and queryString
// json queries could be fun, they are new to me. More like elastic than earlier (bool queries and whatnot)
/*
https://stackoverflow.com/questions/29775797/fetch-post-json-data
  const rawResponse = await fetch('https://httpbin.org/post', {
    method: 'POST',
    headers: {
      'Accept': 'application/json',
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({a: 1, b: 'Textual content'})
  });
  const content = await rawResponse.json();
*/
const solrBaseUrl = 'http://localhost:8983/solr/playin/select'
const queryBody =
{
   "query": {
     "edismax": {
       "query": "${QUERY:*}",
       "qf": [
         "title",
         "composer",
         "pitches",
         "pitches_relative"
       ]
     }
   },
   "facet": {
     "composer": {
       "type": "terms",
       "field": "composer_facet",
       "domain": { "query" : "*:*"}
     }
   },
   "filter": {
     "#tag": "composer:${COMPOSER:*}"
   },
   "params": {
     "hl": "true"
   }
 }



// Build solr query url
const queryString = window.location.search;
const urlParams = new URLSearchParams(queryString);

const queryParamName = 'query';
const composerParamName = 'composer';

var solrUrl = new URL(solrBaseUrl);

if (urlParams.has(queryParamName)) {
  solrUrl.searchParams.set("QUERY", urlParams.get(queryParamName))
}
if (urlParams.has(composerParamName)) {
  solrUrl.searchParams.set("COMPOSER", urlParams.get(composerParamName))
}

fetch(solrUrl , {
    method: 'POST',
    headers: {
      'Accept': 'application/json',
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(queryBody)
  })
  .then( response => {
      if (!response.ok) {
        throw new Error(`HTTP error: ${response.status}`);
      }
      return response.json();
    })
    .then( json => initialize(json) )
    .catch( err => console.error(`Fetch problem: ${err.message}`) );

// use fetch to retrieve the query response and pass it to init
// report any errors that occur in the fetch operation
// once the response has been successfully loaded and formatted as a JSON object
// using response.json(), run the initialize() function

/*fetch('response_json_facet.json') // TODO actual call to solr
  .then( response => {
    if (!response.ok) {
      throw new Error(`HTTP error: ${response.status}`);
    }
    return response.json();
  })
  .then( json => initialize(json) )
  .catch( err => console.error(`Fetch problem: ${err.message}`) );
*/
// sets up the app logic, declares required variables, contains all the other functions
function initialize(response) {
  // grab the UI elements that we need to manipulate
  const composerSelector = document.querySelector('#composer');
  const searchBox = document.getElementById('searchBox');
  const searchBtn = document.querySelector('button');
  const main = document.querySelector('main');

  if (urlParams.has(queryParamName)) {
    searchBox.value = urlParams.get(queryParamName);
  }
  

  const matchingSongs = response.response.docs;
  const composerFacets = response.facets.composer.buckets;

  updateDisplay();

  // when the search button is clicked, invoke selectCategory() to start
  // a search running to select the category of products we want to display
  searchBtn.addEventListener('click', reloadWithNewSearch);

  function reloadWithNewSearch(e) {
    // Use preventDefault() to stop the form submitting — that would ruin
    // the experience
    e.preventDefault();
    
    if (!(searchBox.value.trim() === '')) {
      urlParams.set(queryParamName, searchBox.value);
    } else {
      urlParams.delete(queryParamName);
    }

    if (composerSelector.value === 'All') {
      urlParams.delete('composer')
    } else {
      urlParams.set('composer', composerSelector.value)
    }
    
    window.location.search = urlParams.toString();
  }

  // start the process of updating the display with the new set of products
  function updateDisplay() {

    for (const composer of composerFacets) {
        const option = document.createElement('option');
        option.value = composer.val;
        option.innerHTML = composer.val + " (" + composer.count +")";
        composerSelector.appendChild(option);
    }

    // Update selected facet in the dropdown.
    if (urlParams.has(composerParamName)) {
      composerSelector.value = urlParams.get(composerParamName);
      // If composer param is supplied but doesn't match a facet returned.
      if(!composerSelector.value) {
        const option = document.createElement('option');
        option.value = urlParams.get(composerParamName);
        option.innerHTML = urlParams.get(composerParamName) + " (0)";
        composerSelector.appendChild(option);
        composerSelector.value = urlParams.get(composerParamName);
      }
    }

    // if no songs match the search term, display a "No results to display" message
    if (matchingSongs.length === 0) {
      const para = document.createElement('p');
      para.textContent = 'No results to display!';
      main.appendChild(para);
    } else {
      for (const song of matchingSongs) {
        showSong(song);
      }
    }
  }
/*
  // fetchBlob uses fetch to retrieve the image for that product, and then sends the
  // resulting image display URL and product object on to showSong() to finally
  // display it
  function fetchBlob(product) {
    // construct the URL path to the image file from the product.image property
    const url = `images/${product.image}`;
    // Use fetch to fetch the image, and convert the resulting response to a blob
    // Again, if any errors occur we report them in the console.
    fetch(url)
      .then( response => {
        if (!response.ok) {
          throw new Error(`HTTP error: ${response.status}`);
        }
        return response.blob();
      })
      .then( blob => showProduct(blob, product) )
      .catch( err => console.error(`Fetch problem: ${err.message}`) );
  }*/

  // Display a product inside the <main> element
  function showSong(song) {
    // Convert the blob to an object URL — this is basically an temporary internal URL
    // that points to an object stored inside the browser
    //const objectURL = URL.createObjectURL(blob);
    // create <section>, <h2>, <p>, and <img> elements
    const section = document.createElement('section');
    const heading = document.createElement('h2');
    const para = document.createElement('p');
    //const image = document.createElement('img');

    // give the <section> a classname equal to the product "type" property so it will display the correct icon
    section.setAttribute('class', 'song');
    //TODO hmm

    // Give the <h2> textContent equal to the product "name" property, but with the first character
    // replaced with the uppercase version of the first character
    heading.textContent = song.title[0];
    // TODO change when tilte is singlevalued

    // Give the <p> textContent equal to the product "price" property, with a $ sign in front
    // toFixed(2) is used to fix the price at 2 decimal places, so for example 1.40 is displayed
    // as 1.40, not 1.4.
    para.textContent = song.composer[0];
    // TODO multiValued

    // Set the src of the <img> element to the ObjectURL, and the alt to the product "name" property
    //image.src = objectURL;
    //image.alt = product.name;

    // append the elements to the DOM as appropriate, to add the product to the UI
    main.appendChild(section);
    section.appendChild(heading);
    section.appendChild(para);
    //section.appendChild(image);
  }
}
