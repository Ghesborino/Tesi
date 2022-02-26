import os
import sys
import time
import urllib
import threading
import multiprocessing

from urllib import request

"""
def io_bound(url_address : str, ripetizioni : int, indice : int):

    with open(f'output{indice}.txt', 'r') as input:
        page = input.read()
        for _ in range(ripetizioni):
            with open(f'output{indice}.txt', 'w') as output:
                output.write(str(page))
"""

def io_bound(url_address : str, ripetizioni : int, indice : int):

    with urllib.request.urlopen(url_address, timeout=20) as conn:
        page = conn.read()
        for _ in range(ripetizioni):
            with open(f'output{indice}.txt', 'w') as output:
                output.write(str(page))


def mt(ripetizioni : int, esecutori : int) -> float :

    tempo_zero = time.perf_counter()

    threads = []
    for i in range(esecutori):
        threads.append(threading.Thread(target=io_bound, args=["http://www.google.it", ripetizioni, i]))

    for t in threads:
        t.start()

    for t in threads:
        t.join()

    tempo_finale = time.perf_counter()

    return (tempo_finale - tempo_zero)


def mp(ripetizioni : int , esecutori : int) -> float :

    tempo_zero = time.perf_counter()

    processi = []
    for i in range(esecutori):
        processi.append(multiprocessing.Process(target=io_bound, args=["http://www.google.it", ripetizioni, i]))

    for p in processi:
        p.start()

    for p in processi:
        p.join()

    tempo_finale = time.perf_counter()

    return (tempo_finale - tempo_zero)


if __name__ == '__main__' :

    ripetizioni = 100 if sys.argv.__len__() < 3 else int(sys.argv[1])  # numero di richieste complessive
    esecutori = os.cpu_count() if sys.argv.__len__() < 3 else int(sys.argv[2])  # numero di thread/processi

    threading_time = mt(ripetizioni, esecutori)  # ottengo il tempo impiegato dal metodo con multithreading
    processing_time = mp(ripetizioni, esecutori)  # ottengo il tempo impiegato dal metodo con multiprocessing

    print(f"{'{'}\"ripetizioni\":{ripetizioni}, \"esecutori\":{esecutori}, \"multithreading\":{threading_time}, \"multiprocessing\":{processing_time}{'}'}")
