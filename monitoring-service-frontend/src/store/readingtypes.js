import axios from 'axios'
import { url } from '@/utils/url.js'

export default {
    state: {
        types: {}
    },
    mutations: {
        setTypes(state, types) {
            state.types = types
        },
        clearInfo(state) {
            state.types = {}
        }
    },
    actions: {
        async fetchTypes(context) {
            let types = {}
            try {
                await axios.get(url + 'reading-type/types', { 'headers': { 'Authorization': 'Bearer ' + localStorage.getItem('token') } }
            ).then(response => {
                
                types = response.data.data
                
                context.commit('setTypes', types)
              })
              .catch(error => {
                commit('setError', error)
                throw error;
              });
            } catch (e) {
                console.log(e)
                throw e;
            }
        },
        async addtype({dispatch, commit}, {type}) {
            try {
                await axios.post(url + 'reading-type/add', {
                type: type
            }, { 'headers': { 'Authorization': 'Bearer ' + localStorage.getItem('token') } })
                    .then(response => {
                M.toast({html: response.data.message})
              })
              .catch(error => {
                if (axios.isAxiosError(error)) {
                    M.toast({html: error.response.data.error})
                  }
                commit('setError', error)
                throw error;
              });
            } catch (e) {
                console.log(e)
                throw e;
            }
        },
    },
    getters: {
        types: s => s.types
    }
}