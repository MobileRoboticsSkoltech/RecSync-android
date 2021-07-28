import time
import os, errno
from src.RemoteControl import RemoteControl
from datetime import datetime

HOST = '192.168.43.1'  # The smartphone's IP address


def main():
    # example class usage
    # constructor starts the connection
    remote = RemoteControl(HOST)
    print("Connected")

    dt_string = datetime.now().strftime("%d%m%Y%H%M%S")
    try:
        os.makedirs(dt_string)
    except OSError as e:
        if e.errno != errno.EEXIST:
            raise

    # receives last video (blocks until received)
    start = time.time()
    hostnames = remote.get_hostnames()
    print(hostnames)
    remote.get_video(want_progress_bar=True,
                     custom_filename="leader.mp4", subdir=dt_string)
    end = time.time()
    print("elapsed: %f" % (end - start))

    data, ts_fname = remote.get_ts_file()
    with open(os.path.join(dt_string, 'leader.csv'), "w+") as leader:
        leader.writelines(data)
    print('Closing connection')
    remote.close()

    hostnames = hostnames[:-1]
    # get client videos:
    for i, hostname in enumerate(hostnames):
        remote = RemoteControl(hostname)
        print("Connected")

        start = time.time()
        cl_fname = f"client{i}"
        remote.get_video(want_progress_bar=True,
                         custom_filename=f'{cl_fname}.mp4', subdir=dt_string)
        end = time.time()
        print("elapsed: %f" % (end - start))

        data, ts_fname = remote.get_ts_file()
        with open(os.path.join(dt_string, f'{cl_fname}.csv'), "w+") as client:
            client.writelines(data)
        print('Closing connection')
        remote.close()


if __name__ == '__main__':
    main()
