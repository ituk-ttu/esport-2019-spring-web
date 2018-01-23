import Vue from 'vue';
import VueResource from 'vue-resource';
import Showdown from 'showdown';

Vue.use(VueResource);

function DocumentService (Vue) {
  const svc = {};

  const init = () => {};

  const converter = new Showdown.Converter();
  converter.setOption('disableForced4SpacesIndentedSublists', true);

  svc.getDocument = (name, language) => {
    return Vue.http.get(window.location.origin + '/static/documents/{name}.{language}.md',
                        { params: { name, language } })
                   .then(res => converter.makeHtml(res.body));
  };

  init();

  return svc;
}

const install = (Vue, options) => {
  let service = DocumentService(Vue);
  Vue.prototype.$document = service;
  Vue.$document = service;
};

export default { install };
