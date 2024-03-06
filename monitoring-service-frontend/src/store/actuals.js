import axios from 'axios'
import { url } from '@/utils/url.js'

export default {
    state: {
        actuals: {}
    },
    mutations: {
        setActuals(state, actuals) {
            state.actuals = actuals
        },
        clearActuals(state) {
            state.actuals = {}
        }
    },
    actions: {
        async fetchActuals(context) {
            let actuals = {}
            try {
                await axios.get(url + 'reading/actual', { 'headers': { 'Authorization': 'Bearer ' + localStorage.getItem('token') } }
            ).then(response => {
                
                actuals = response.data.data
                
                context.commit('setActuals', actuals)
              })
              .catch(error => {
                commit('setError', error)
                throw error;
              });
            } catch (e) {
                console.log(e)
                throw e;
            }
        }
    
    },
    getters: {
        actuals: s => s.actuals
    }
}