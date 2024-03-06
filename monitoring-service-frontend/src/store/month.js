import axios from 'axios'
import { url } from '@/utils/url.js'

export default {
    state: {
        months: {}
    },
    mutations: {
        setMonth(state, months) {
            state.months = months
        },
        clearMonth(state) {
            state.months = {}
        }
    },
    actions: {
        async month(context, {month}) {
            let months = {}
            try {
                await axios.get(url + 'reading/month?monthNumber=' + month, { 'headers': { 'Authorization': 'Bearer ' + localStorage.getItem('token') } }
            ).then(response => {
                
                months = response.data.data
                
                context.commit('setMonth', months)
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
        }
    
    },
    getters: {
        months: s => s.months
    }
}