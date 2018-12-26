// https://eslint.org/docs/user-guide/configuring

const OFF = 0;
const WARNING = 1;
const ERROR = 2;

module.exports = {
  root: true,
  parser: 'babel-eslint',
  parserOptions: {
    sourceType: 'module'
  },
  env: {
    browser: true
  },
  // https://github.com/standard/standard/blob/master/docs/RULES-en.md
  extends: 'standard',
  // required to lint *.vue files
  plugins: [
    'html'
  ],
  // add your custom rules here
  rules: {
    // allow async-await
    'generator-star-spacing': 'off',
    // allow debugger during development
    'no-debugger': process.env.NODE_ENV === 'production' ? 'error' : 'off',
    "semi": [ERROR, "always"],
    "operator-linebreak": [WARNING, "after"],
    "space-before-function-paren": [OFF, {
      "anonymous": "never",
      "named": "never",
      "asyncArrow": "always"
    }]
  }
};
