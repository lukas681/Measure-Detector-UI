# Using Annotorius with Angular

The following software versions have been used:

| Module  | Version |
|---------|---------|
| Angular | 12.2.   |
| Annotorius Version | 2.6.0 |
|TS Version | 4.3.5 |

> Note: The front-end was fully generated with jHipster.

## Integrating Annotorius into an Angular Component

1) Install via npm.

        npm install @recogito/annotorious

2) As there are no Type Definitions available., we have to import them with 'any' type in order to run. Therefore, add a new file in the root directory of the webapplication and call it `descs.d.ts` with the following content:

         declare module '@recogito/annotorious'

3) include the CSS file in `global.scss`. In my case it did not suffice to use the @import statement just in the component. It simply did not load.
   

      @import '~@recogito/annotorious/dist/annotorious.min.css';

5) Create a new Annotorius object like shown on the official documentation. 

       const anno = new Annotorious ({
         image: document.getElementById('test')
       });

       anno.on('createAnnotation', function(annotation: any):any {
         console.warn('Created!');
       });

Further Sources:

* https://medium.com/@steveruiz/using-a-javascript-library-without-type-declarations-in-a-typescript-project-3643490015f3
