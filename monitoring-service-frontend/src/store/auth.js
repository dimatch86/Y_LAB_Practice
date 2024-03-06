
import axios from 'axios'
import { url } from '@/utils/url.js'

export default {
    actions: {
        async login({dispatch, commit}, {email, password}) {
            try {
                await axios.post(url + 'auth/login', {
                email: email,
                password: password
            }).then(response => {
                console.log(response.data.data.email)
                localStorage.setItem('monitoring', response.data.data.email);
                localStorage.setItem('token', response.data.data.token)
              })
              .catch(error => {
                  if (axios.isAxiosError(error)) {
                      M.toast({html: 'Пользователь не найден. Необходима регистрация'})
                  }
                commit('setError', error)
                throw error;
              });
            } catch (e) {
                console.log(e)
                throw e;
            }
        },
        async register({dispatch, commit}, {email, password, role}) {
            try {
                await axios.post(url + 'auth/register', {
                email: email,
                password: password,
                role: role
            }).then(response => {
            
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

        async logout({commit}) {
            console.log(url);
            await axios.get(url + 'auth/logout', { 'headers': { 'Authorization': 'Bearer ' + localStorage.getItem('token') } }
            ).then(() => {
                commit('clearInfo')
                commit('clearActuals')
                commit('clearHistory')
                commit('clearMonth')
                commit('clearAudit')
                localStorage.clear()
              }).catch(error => {
                  localStorage.clear()
            })
              
        }
    }
}