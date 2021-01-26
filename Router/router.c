#include <sys/types.h>
#include <sys/socket.h>
#include <stdio.h>
#include <netdb.h>
#include <arpa/inet.h>
#include <strings.h>
#include <string.h>
#include <unistd.h>
#include <signal.h>
#include <stdlib.h>
#include <errno.h>
#include <sys/select.h>
#include <pthread.h>
#include "ne.h"
#include "router.h"

#define MAXLINE 1024
/* ------ Global Variables ------ */
int fd, ne_port, router_port, id;
unsigned int addr_len;
struct sockaddr_in ne_addr;
struct pkt_INIT_RESPONSE res;
pthread_mutex_t lock;
time_t timers[MAX_ROUTERS];
time_t last_change;
time_t last_published;
time_t start_time;
int converged;
FILE *fp;

int UdpSetup(int port);
void intHandler(int dummy);

int UdpSetup(int port)
{
    int listenfd, optval = 1;
    struct sockaddr_in serveraddr;

    /* Create a socket descriptor */
    if ((listenfd = socket(AF_INET, SOCK_DGRAM, 0)) < 0)
        return -1;
    /* Eliminates "Address already in use" error from bind. */
    if (setsockopt(listenfd, SOL_SOCKET, SO_REUSEADDR,
                   (const void *)&optval, sizeof(int)) < 0)
        return -1;

    /* Listenfd will be an endpoint for all requests to port 
     on any IP address for this host */
    bzero((char *)&serveraddr, sizeof(serveraddr));
    serveraddr.sin_family = AF_INET;
    serveraddr.sin_addr.s_addr = htonl(INADDR_ANY);
    serveraddr.sin_port = htons((unsigned short)port);
    if (bind(listenfd, (struct sockaddr *)&serveraddr, sizeof(serveraddr)) < 0)
        return -1;

    return listenfd;
}

void *polling_fn(void *args)
{
    struct pkt_RT_UPDATE update;
    int nbr_cost;
    int i;
    int isUpdated;
    while (1)
    {
        isUpdated = 0;
        recvfrom(fd, &update, sizeof(update), 0, (struct sockaddr *)&ne_addr, &addr_len);
        ntoh_pkt_RT_UPDATE(&update);
        for (i = 0; i < res.no_nbr; i++)
        {
            if (res.nbrcost[i].nbr == update.sender_id)
            {
                nbr_cost = res.nbrcost[i].cost;
            }
        }
        pthread_mutex_lock(&lock);
        timers[update.sender_id] = time(NULL);
        isUpdated = UpdateRoutes(&update, nbr_cost, id);
        if (isUpdated)
        {
            last_change = time(NULL);
            converged = 0;
            PrintRoutes(fp, id);
            fflush(fp);
        }
        pthread_mutex_unlock(&lock);
    }
    return NULL;
}

void *timer_fn(void *args)
{
    int i;
    while (1)
    {
        pthread_mutex_lock(&lock);
        for (i = 0; i < MAX_ROUTERS; i++)
        {
            if (timers[i] != 0)
            {
                if (difftime(time(NULL), timers[i]) >= FAILURE_DETECTION)
                {
                    UninstallRoutesOnNbrDeath(i);
                    timers[i] = (time_t)0;
                    last_change = time(NULL);
                    converged = 0;
                    PrintRoutes(fp, id);
                    fflush(fp);
                }
            }
        }
        if (difftime(time(NULL), last_published) >= UPDATE_INTERVAL)
        {
            struct pkt_RT_UPDATE update;
            for (i = 0; i < res.no_nbr; i++)
            {
                ConvertTabletoPkt(&update, id);
                update.dest_id = res.nbrcost[i].nbr;
                hton_pkt_RT_UPDATE(&update);
                sendto(fd, &update, sizeof(update), 0,
                       (struct sockaddr *)&ne_addr, sizeof(ne_addr));
            }
            last_published = time(NULL);
        }
        if (difftime(time(NULL), last_change) >= CONVERGE_TIMEOUT && converged == 0)
        {
            fprintf(fp, "%d:Converged\n\n", (int) difftime(time(NULL), start_time));
            fflush(fp);
            converged = 1;
        }
        pthread_mutex_unlock(&lock);
    }
}

void intHandler(int dummy) {
    fclose(fp);
    exit(0);
}

int main(int argc, char **argv)
{
    int i;
    struct hostent *hp;
    struct pkt_INIT_REQUEST req;
    char *hostname;
    signal(SIGINT, intHandler);

    // Initialize lock
    pthread_mutex_init(&lock, NULL);
    id = atoi(argv[1]);
    hostname = argv[2];
    ne_port = atoi(argv[3]);
    router_port = atoi(argv[4]);
    if ((fd = UdpSetup(router_port)) == -1)
    {
        printf("udp setup failed\n");
        return -1;
    }
    memset(&ne_addr, 0, sizeof(ne_addr));

    if ((hp = gethostbyname(hostname)) == NULL)
    {
        printf("getHostByName failed");
        return -1;
    }
    // Setup the destination address and protocol
    bcopy((char *)hp->h_addr,
          (char *)&ne_addr.sin_addr.s_addr, hp->h_length);
    ne_addr.sin_family = AF_INET;
    ne_addr.sin_port = htons(ne_port);
    // Construct the packet
    req.router_id = ntohl(id);
    // Send packet
    sendto(fd, &req, sizeof(req), 0,
           (struct sockaddr *)&ne_addr, sizeof(ne_addr));
    addr_len = sizeof(ne_addr);
    recvfrom(fd, &res, sizeof(res), 0, (struct sockaddr *)&ne_addr, &addr_len);
    ntoh_pkt_INIT_RESPONSE(&res);
    InitRoutingTbl(&res, id);
    for (i = 0; i < MAX_ROUTERS; i++)
    {
        timers[i] = (time_t)0;
    }
    last_change = time(NULL);
    last_published = time(NULL);
    start_time = time(NULL);
    converged = 0;
    char filename[] = "router .log";
    filename[6] = id + '0';
    fp = fopen(filename, "w");
    PrintRoutes(fp, id);
    fflush(fp);
    fclose(fp);
    fp = fopen(filename, "a");

    pthread_t udp_thread;
    pthread_t timer_thread;
    pthread_create(&udp_thread, NULL, polling_fn, NULL);
    pthread_create(&timer_thread, NULL, timer_fn, NULL);
    pthread_join(udp_thread, NULL);
    pthread_join(timer_thread, NULL);

    return 1;
}
