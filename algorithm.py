from scipy import sparse
import numpy as np
import random
import loadMatrix

def FindBiggestN(data, N=1):
    biggestN = list()
    count = 0
    copy_data = np.array(data)
    for i in range(len(copy_data)):
        if count < N:
            index = np.argmax(copy_data)
            biggestN.append(index)
            copy_data[index] = 0
            count = count +1
    return biggestN

def Gen_rand_sparse_mat(m, n, density):
    M = sparse.rand(m, n, density=density)
    M.data[:] = 1
    return M

##Greedy
def QuestionSellectionAlgo_greedy(X, w, qs, ITEM_COUNT):
    X_qs = X.toarray()[:, qs]
    # print X_qs
    E = np.dot(w, X_qs) / ITEM_COUNT
    q = np.argmin(np.fabs(E - 0.5))
    return qs[q]

def jump_prob(e, enew, T):
    if enew < e: 
        return 1
    else:
        return np.exp((e-enew)/T)

def compute_min_d(X_qk, GAMMA, w, ls):
    ls_ds = []
    for l in ls:
        dcs = [np.bitwise_xor(l, X_qk.astype(int)).sum()]
        d = (w*[1 - GAMMA**dc for dc in dcs]).sum()
        ls_ds.append(d)
    return np.min(ls_ds)

#ihs_sa
def questionSellectionAlgo_ihs_sa(X, GAMMA, w, qs, k, I, T=100):
    ls = (((np.array(range(2**k))[:,None] & (1 << np.arange(k)))) > 0).astype(int)
    qk = np.random.choice(qs, k, replace=False)
    X_qk = X.toarray()[:, qk]
    min_d = compute_min_d(X_qk, GAMMA, w, ls)
    t = T
    
    qk_best = qk
    for i in range(I):
        qk_n = np.array(qk)
        qk_n[np.random.choice(k)] = np.random.choice(np.setdiff1d(qs, qk))
        X_qk_n = X.toarray()[:, qk_n]
        min_d_n = compute_min_d(X_qk_n, GAMMA, w, ls)
        
        t = t*0.9
        if jump_prob(min_d, min_d_n, t) > np.random.rand(1):
            qk = qk_n
        if min_d_n > min_d:
            min_d = min_d_n
            qk_best = qk_n
    return qk_best

def InteractiveRecommandation(X, T, GAMMA, ans, w, r_qs, algr, ITEM_COUNT, TAGS,q_array=None):
    if algr == 0:
        q = QuestionSellectionAlgo_greedy(X, w, r_qs, ITEM_COUNT)
        # def QuestionSellectionAlgo_greedy(X, w, qs):
    elif algr == 1:
        q = questionSellectionAlgo_ihs_sa(X, GAMMA, w, r_qs, 2, ITEM_COUNT)[0]
        # def questionSellectionAlgo_ihs_sa(X, GAMMA, w, qs, k, I, T):
    elif algr == 2:
        if len(q_array) == 0:
            q_array = questionSellectionAlgo_ihs_sa(X, GAMMA, w, r_qs, 2, ITEM_COUNT)
            q = q_array[0]
            q_array = np.delete(q_array , 0)
        else:
            q = q_array[0]
            q_array = np.delete(q_array , 0)

    #delete index
    r_qs = r_qs[r_qs != q]
    X_q = X.toarray()[:, q]
    # print 'select', TAGS[q], ', X(q): ', X_q
    
    g = np.ones(ITEM_COUNT)
    if ans == '1':
        g = X_q
    elif ans == '0':
        g = (1 - X_q)

    #tag_g == 0, set decrease
    g[g == 0] = GAMMA
    #decrease weight
    w = w*g
    if algr == 0 or algr == 1:
        return w, r_qs, TAGS[q]
    else:
        return w, r_qs, TAGS[q], q_array

