import axios, { AxiosError } from 'axios'
import { url } from '@/utils/url.js'
import { helpers } from 'vuelidate/lib/validators';

export default {
    actions: {
        async send({dispatch, commit}, {type, value}) {
        
            try {
                await axios.post(url + 'reading/send', {
                type: type,
                value: value
            }, { 'headers': { 'Authorization': 'Bearer ' + localStorage.getItem('token') } })
                    .then(response => {
                console.log('LOGIN');
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
                console.log('И эта ' + e)
                throw e;
            }
        },
    }
}