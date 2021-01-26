#include "ne.h"
#include "router.h"

/* ----- GLOBAL VARIABLES ----- */
struct route_entry routingTable[MAX_ROUTERS];
int NumRoutes;

void UpdatePath(unsigned int *host_path, unsigned int *incoming_path, unsigned int path_len, int neighborId);
int GetRouteEntryIndex(int route_id);
int isInPath(struct route_entry *entry, int id);

////////////////////////////////////////////////////////////////
void InitRoutingTbl(struct pkt_INIT_RESPONSE *InitResponse, int myID)
{
	/* ----- YOUR CODE HERE ----- */
	int i;
	// Initialize own entry
	routingTable[0].dest_id = (unsigned int)myID;
	routingTable[0].next_hop = (unsigned int)myID;
	routingTable[0].cost = 0;
	routingTable[0].path_len = 1;
	routingTable[0].path[0] = (unsigned int)myID;
	// Initialize neighbors
	for (i = 0; i < InitResponse->no_nbr; i++)
	{
		routingTable[i + 1].dest_id = InitResponse->nbrcost[i].nbr;
		routingTable[i + 1].next_hop = InitResponse->nbrcost[i].nbr;
		routingTable[i + 1].cost = InitResponse->nbrcost[i].cost;
		routingTable[i + 1].path_len = 2;
		routingTable[i + 1].path[0] = (unsigned int)myID;
		routingTable[i + 1].path[1] = (unsigned int)InitResponse->nbrcost[i].nbr;
	}
	NumRoutes = 1 + InitResponse->no_nbr;
	return;
}

////////////////////////////////////////////////////////////////
int UpdateRoutes(struct pkt_RT_UPDATE *RecvdUpdatePacket, int costToNbr, int myID)
{
	/* ----- YOUR CODE HERE ----- */
	int isChanged = 0;
	int i;
	for (i = 0; i < RecvdUpdatePacket->no_routes; i++)
	{
		// Update one table entry at a time
		struct route_entry re = RecvdUpdatePacket->route[i];
		// Determine the cost
		int cost;

		cost = (re.cost + costToNbr) >= INFINITY ? INFINITY: (re.cost + costToNbr);
		// If the destination is unknown
		if (GetRouteEntryIndex(re.dest_id) == -1)
		{
			routingTable[NumRoutes].dest_id = re.dest_id;
			routingTable[NumRoutes].cost = cost;
			routingTable[NumRoutes].next_hop = RecvdUpdatePacket->sender_id;
			UpdatePath(routingTable[NumRoutes].path, re.path, re.path_len, myID);
			routingTable[NumRoutes].path_len = re.path_len + 1;
			isChanged = 1;
			NumRoutes++;
			continue;
		}
		// Check if self is contained in the path. If so, do nothing
		if(isInPath(&re, myID)) {
			continue;
		}
		// If self is not contained in the route, check if it is better to update or requires forced update
		int destIndex = GetRouteEntryIndex(re.dest_id);
		if ((routingTable[destIndex].cost > re.cost + costToNbr) ||
			(routingTable[destIndex].next_hop == RecvdUpdatePacket->sender_id && cost > routingTable[destIndex].cost))
		{
			routingTable[destIndex].dest_id = re.dest_id;
			routingTable[destIndex].cost = cost;
			routingTable[destIndex].next_hop = RecvdUpdatePacket->sender_id;
			routingTable[destIndex].path_len = re.path_len + 1;
			UpdatePath(routingTable[destIndex].path, re.path, re.path_len, myID);
			isChanged = 1;
			continue;
		}
	}
	return isChanged;
}

void UpdatePath(unsigned int *host_path, unsigned int *incoming_path, unsigned int path_len, int myID)
{
	int i;
	for (i = path_len; i > 0; i--)
	{
		host_path[i] = incoming_path[i - 1];
	}
	host_path[0] = (unsigned int)myID;
}

int GetRouteEntryIndex(int route_id)
{
	int i;
	for (i = 0; i < NumRoutes; i++)
	{
		if (routingTable[i].dest_id == (unsigned int)route_id)
		{
			return i;
		}
	}
	return -1;
}

void UninstallRoutesOnNbrDeath(int DeadNbr)
{
	int i;
	for (i = 0; i < NumRoutes; i++)
	{
		if (isInPath(&routingTable[i], DeadNbr))
		{
			routingTable[i].cost = (unsigned int)INFINITY;
		}
	}
}

int isInPath(struct route_entry *entry, int id)
{
	int i;
	for (i = 0; i < entry->path_len; i++)
	{
		if (entry->path[i] == (unsigned int)id)
		{
			return 1;
		}
	}
	return 0;
}

////////////////////////////////////////////////////////////////
void ConvertTabletoPkt(struct pkt_RT_UPDATE *UpdatePacketToSend, int myID)
{
	int i;
	UpdatePacketToSend->sender_id = (unsigned int)myID;
	UpdatePacketToSend->no_routes = NumRoutes;
	for (i = 0; i < NumRoutes; i++)
	{
		UpdatePacketToSend->route[i] = routingTable[i];
	}
	return;
}

////////////////////////////////////////////////////////////////
//It is highly recommended that you do not change this function!
void PrintRoutes(FILE *Logfile, int myID)
{
	/* ----- PRINT ALL ROUTES TO LOG FILE ----- */
	int i;
	int j;
	for (i = 0; i < NumRoutes; i++)
	{
		fprintf(Logfile, "<R%d -> R%d> Path: R%d", myID, routingTable[i].dest_id, myID);

		/* ----- PRINT PATH VECTOR ----- */
		for (j = 1; j < routingTable[i].path_len; j++)
		{
			fprintf(Logfile, " -> R%d", routingTable[i].path[j]);
		}
		fprintf(Logfile, ", Cost: %d\n", routingTable[i].cost);
	}
	fprintf(Logfile, "\n");
	fflush(Logfile);
}

////////////////////////////////////////////////////////////////