
const solrBaseUrl = 'http://localhost:8983/solr/playin/select'
const queryBody =
{
  "query": {
    "bool" : {
      "should": [
        {
          "edismax": {
            "query": "${QUERY:*}",
            "qf": [
              "title",
              "composer"
            ]
          }
        },
        {
          "edismax": {
            "query": "\"${QUERY:\"\"}\"",
            "qf": [
              "pitches",
              "pitches_relative"
            ]
          }
        }
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

/*fetch('response_json_facet.json')
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
      urlParams.delete(composerParamName)
    } else {
      urlParams.set(composerParamName, composerSelector.value)
    }
    
    window.location.search = urlParams.toString();
  }

  // populate search results and facets
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
      
      // Workaround for if composer param is supplied but doesn't match a facet returned
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

  // Display a song inside the <main> element
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


    // TODO make pretty
    para.textContent = song.composer.join(',\n');
    

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
