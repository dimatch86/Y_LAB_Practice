import axios from 'axios'
import { url } from '@/utils/url.js'

export default {
    state: {
        info: {}
    },
    mutations: {
        setInfo(state, info) {
            state.info = info
        },
        clearInfo(state) {
            state.info = {}
        }
    },
    actions: {
        async fetchInfo(context) {
            let info = {}
            try {
                info = await axios.get(url + 'auth/info', { 'headers': { 'Authorization': 'Bearer ' + localStorage.getItem('token') } }
            ).then(response => {
                
                info = response.data.data
                
                context.commit('setInfo', info)
              })
              .catch(error => {
                commit('setError', error)
                  localStorage.clear()
                throw error;
              });
            } catch (e) {
                console.log(e)
                localStorage.clear()
                throw e;
            }
        }
    },
    getters: {
        info: s => s.info
    }
}