import axios from 'axios'
import { url } from '@/utils/url.js'

export default {
    state: {
        history: {}
    },
    mutations: {
        setHistory(state, history) {
            state.history = history
        },
        clearHistory(state) {
            state.history = {}
        }
    },
    actions: {
        async fetchHistory(context) {
            let history = {}
            try {
                await axios.get(url + 'reading/history', { 'headers': { 'Authorization': 'Bearer ' + localStorage.getItem('token') } }
            ).then(response => {
                history = response.data.data
                context.commit('setHistory', history)
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
        history: s => s.history
    }
}