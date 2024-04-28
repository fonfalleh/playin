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
const solrUrl = 'http://localhost:8983/solr/playin/select'
//const solrUrl = 'http://solr:8983/solr/playin/select'
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
     "#tag": "composer:${COMOPSER:*}"
   },
   "params": {
     "hl": "true"
   }
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
  const category = document.querySelector('#composer');
  const searchTerm = document.querySelector('#searchTerm');
  const searchBtn = document.querySelector('button');
  const main = document.querySelector('main');

  // TODO
  const queryString = window.location.search;
  const urlParams = new URLSearchParams(queryString);
  const selectedComposer = urlParams.get('composer')

  // keep a record of what the last category and search term entered were
  let lastCategory = category.value;
  // no search has been made yet
  let lastSearch = '';

  // these contain the results of filtering by category, and search term
  // finalGroup will contain the products that need to be displayed after
  // the searching has been done. Each will be an array containing objects.
  // Each object will represent a product
  let categoryGroup;
  let finalGroup;

  // To start with, set finalGroup to equal the entire products database
  // then run updateDisplay(), so ALL products are displayed initially.
  let products = response.response.docs; // TODO lol testing
  let composerFacets = response.facets.composer.buckets;
  finalGroup = products;
  updateDisplay();

  // Set both to equal an empty array, in time for searches to be run
  categoryGroup = [];
  finalGroup = [];

  // when the search button is clicked, invoke selectCategory() to start
  // a search running to select the category of products we want to display
  searchBtn.addEventListener('click', selectCategory);

  function selectCategory(e) {
    // Use preventDefault() to stop the form submitting — that would ruin
    // the experience
    e.preventDefault();

    // Set these back to empty arrays, to clear out the previous search
    categoryGroup = [];
    finalGroup = [];

    // if the category and search term are the same as they were the last time a
    // search was run, the results will be the same, so there is no point running
    // it again — just return out of the function
    if (category.value === lastCategory && searchTerm.value.trim() === lastSearch) {
      return;
    } else {
      // update the record of last category and search term
      lastCategory = category.value;
      lastSearch = searchTerm.value.trim();
      // In this case we want to select all products, then filter them by the search
      // term, so we just set categoryGroup to the entire JSON object, then run selectProducts()
      if (category.value === 'All') {

        categoryGroup = products;
        selectProducts();
      // If a specific category is chosen, we need to filter out the products not in that
      // category, then put the remaining products inside categoryGroup, before running
      // selectProducts()
      } else {
        // the values in the <option> elements are uppercase, whereas the categories
        // store in the JSON (under "type") are lowercase. We therefore need to convert
        // to lower case before we do a comparison
        const lowerCaseType = category.value.toLowerCase();
        // Filter categoryGroup to contain only products whose type includes the category
        categoryGroup = products.filter( product => product.type === lowerCaseType );

        // Run selectProducts() after the filtering has been done
        selectProducts();
      }
    }
  }

  // selectProducts() Takes the group of products selected by selectCategory(), and further
  // filters them by the tiered search term (if one has been entered)
  function selectProducts() {
    // If no search term has been entered, just make the finalGroup array equal to the categoryGroup
    // array — we don't want to filter the products further.
    if (searchTerm.value.trim() === '') {
    // TODO This is the nice path
      finalGroup = categoryGroup;
    } else {
    // TODO search stoff
      // Make sure the search term is converted to lower case before comparison. We've kept the
      // product names all lower case to keep things simple
      const lowerCaseSearchTerm = searchTerm.value.trim().toLowerCase();
      // Filter finalGroup to contain only products whose name includes the search term
      finalGroup = categoryGroup.filter( product => product.name.includes(lowerCaseSearchTerm));
    }
    // Once we have the final group, update the display
    updateDisplay();
  }

  // start the process of updating the display with the new set of products
  function updateDisplay() {
    // remove the previous contents of the <main> element
    while (main.firstChild) {
      main.removeChild(main.firstChild);
    }

    for (const composer of composerFacets) {
        const option = document.createElement('option');
        option.value = composer.val;
        option.innerHTML = composer.val + " (" + composer.count +")";
        category.appendChild(option);
    }

    // if no products match the search term, display a "No results to display" message
    if (finalGroup.length === 0) {
      const para = document.createElement('p');
      para.textContent = 'No results to display!';
      main.appendChild(para);
    // for each product we want to display, pass its product object to fetchBlob()
    } else {
      for (const song of finalGroup) {
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
