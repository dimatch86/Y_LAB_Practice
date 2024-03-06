import Vue from 'vue'
import Vuex from 'vuex'
import auth from './auth'
import info from './info'
import readingtypes from './readingtypes'
import reading from './reading'
import actuals from './actuals'
import history from './history'
import month from './month'
Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    error: null
  },
  getters: {
  },
  mutations: {
    setError(state, error) {
      state.error = error
    },
    clearError(state) {
      state.error = null
    }
  },
  getters: {
    error: s => s.error
  },
  actions: {
    
  },
  modules: {
    auth, info, readingtypes, reading, actuals, history, month
  }
})
